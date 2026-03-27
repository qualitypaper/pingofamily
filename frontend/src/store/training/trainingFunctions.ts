import { TrainingExampleWithIndex, TrainingType } from "./trainingTypes";

export const addToArray = (wordToAdd: TrainingExampleWithIndex, array: TrainingExampleWithIndex[]) => {
    if (
        array.find(
            (elem) =>
                elem.index === wordToAdd.index && elem.trainingExample.id === wordToAdd.trainingExample.id,
        )
    ) {
        return array;
    }

    return [...array, wordToAdd];
};

export function setHintStatus(
    finalTrainingSequence: TrainingExampleWithIndex[],
    index: number,
    trainingType: TrainingType,
) {
    return setStatus(finalTrainingSequence, index, trainingType, "hint");
}

export function setSkippedStatus(
    finalTrainingSequence: TrainingExampleWithIndex[],
    index: number,
    trainingType: TrainingType,
) {
    return setStatus(finalTrainingSequence, index, trainingType, "skipped");
}

export function setStatus(
    array: TrainingExampleWithIndex[],
    index: number,
    trainingType: TrainingType,
    statusKey: string,
): TrainingExampleWithIndex[] {
    return array.map((elem) =>
        elem.index === index && elem.trainingExample.trainingType === trainingType
            ? {
                ...elem,
                [statusKey]: true,
                timestamp: new Date(),
            }
            : { ...elem },
    );
}

export function sortByCategory<T, K extends keyof T>(array: T[], categoryField: K, sequence: T[K][]) {
    // Create a map of category to its priority order
    let priorityMap: Map<T[K], number> = new Map(sequence.map((e, i) => {
        return [e, i]
    }));

    return array.sort((a, b) => {
        // Get the priority of each item's category, default to Infinity if not found
        const priorityA = priorityMap.get(a[categoryField]) ?? Infinity;
        const priorityB = priorityMap.get(b[categoryField]) ?? Infinity;
        
        return priorityA - priorityB;
    });
}