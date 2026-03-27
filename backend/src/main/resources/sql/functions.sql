--model-prediction-functions--


-- current prediction is made with the Ebbinghaus forgetting curve and personalized half-life
-- the function is an identical copy of one used for training a PrioritizationModel
-- used mainly for selecting words for training
create or replace function make_prediction(record word_statistics, lag_time interval)
    returns double precision
    language plpgsql
as
'
    declare
        half_life     double precision;
        model_weights double precision[];
    begin


        select array_agg(weights)
        into model_weights
        from (select weights
              from user_vocabulary_weights
              where word_statistics_id = record.id
              limit 2) sub;

        half_life = half_life(
                ARRAY [
                    log_min_max_normalize(record.total_training_count, 1, 20),
                    log_min_max_normalize(record.average_training_score, 0, 10)
                    ],
                model_weights
                    );

        return minmax(pow(2, -log_min_max_normalize(extract(epoch from lag_time) / 86400, 1, 30) / half_life), 0, 1);
    end;
';

-- the half-life function deferred from the Duolingo paper for Spaced Repetition Learning
create or replace function half_life(variables double precision[], weights double precision[])
    returns double precision
    language plpgsql
as
'
    declare
        sum double precision = 0;

    begin
        if array_length(variables, 1) <> array_length(weights, 1) THEN
            RAISE EXCEPTION ''Length mismatch'';
        end if;

        for i in 1..array_length(variables, 1)
            loop
                sum = sum + variables[i] * weights[i];
            end loop;

        return pow(2, sum);
    end;
';

-- uses log-minmax normalization
create or replace function log_min_max_normalize(val float, min_val float, max_val float)
    returns float
    language plpgsql
as
'
    begin
        if min_val > val then
            return 0.00001;
        end if;
        if val > max_val then
            return 1.0;
        end if;

        return sqrt((log(val + 1) - log(min_val + 1)) / (log(max_val + 1) - log(min_val + 1)));
    end;
';

create or replace function minmax(val float, min_val float, max_val float)
    returns float
    language plpgsql
as
'
    begin
        return least(max_val, greatest(val, min_val));
    end;
';

-- insert_into_word_statistics function
create or replace function insert_into_word_statistics()
    returns bigint
    language plpgsql
as
'
    declare
        id_val bigint;
    begin
        insert into word_statistics (last_week_training_count,
                                     last_month_training_count,
                                     number_of_training_without_mistake,
                                     created_at)
        values (0, 0, 0, extract(epoch from now()))
        returning id into id_val;

        return id_val;
    end;
';
