import {useTranslation} from 'react-i18next';
import {Link} from 'react-router-dom';
import i18n from '../../../i18nf/i18n';
import {useEffect, useRef} from "react";

const PrivacyPolicy = () => {
	const {t} = useTranslation();
	const ref = useRef(null);

	useEffect(() => {
		window.scrollTo(0, 0);
	}, [ref]);

	return (
		<div ref={ref} className="font-sans text-gray-700  max-w-4xl mx-auto p-6">
			<h1
				className="text-4xl text-blue-900 border-b-2 border-blue-500 pb-4 mb-6 font-bold">{t('PrivacyPolicyTitle')}</h1>
			<p className="italic text-gray-500 mb-6">{t('LastUpdated', {date: 'March 01, 2025'})}</p>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<p className="mb-4">{t('IntroDescription')}</p>
				<p>
					{t('IntroConsent')}{' '}
					<Link
						to="https://www.privacypolicies.com/privacy-policy-generator/"
						target="_blank"
						rel="noopener noreferrer"
						className="text-blue-600 hover:text-red-500 hover:underline"
					>
						{t('IntroPrivacyPolicyGenerator')}
					</Link>
				</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('InterpretationAndDefinitionsTitle')}</h2>
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('InterpretationAndDefinitionsInterpretation')}</h3>
				<p className="mb-4">{t('InterpretationAndDefinitionsInterpretationText')}</p>

				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('InterpretationAndDefinitionsDefinitions')}</h3>
				<p className="mb-4">{t('InterpretationAndDefinitionsDefinitionsText')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsAccount')}</strong> {t('DefinitionsAccountText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsAffiliate')}</strong> {t('DefinitionsAffiliateText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsCompany')}</strong> {t('DefinitionsCompanyText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsCookies')}</strong> {t('DefinitionsCookiesText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsCountry')}</strong> {t('DefinitionsCountryText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsDevice')}</strong> {t('DefinitionsDeviceText')}</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsPersonalData')}</strong> {t('DefinitionsPersonalDataText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsService')}</strong> {t('DefinitionsServiceText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsServiceProvider')}</strong> {t('DefinitionsServiceProviderText')}
						</p>
					</li>
					<li className="mb-2">
						<p><strong
							className="text-gray-900">{t('DefinitionsUsageData')}</strong> {t('DefinitionsUsageDataText')}
						</p>
					</li>
					<li className="mb-2">
						<p>
							<strong
								className="text-gray-900">{t('DefinitionsWebsite')}</strong> {t('DefinitionsWebsiteText')}{' '}
							<Link
								to="https://pingo.family"
								rel="external nofollow noopener"
								target="_blank"
								className="text-[#1E90FF] hover:text-red-500 hover:underline"
							>
								https://pingo.family
							</Link>
						</p>
					</li>
					<li className="mb-2">
						<p><strong className="text-gray-900">{t('DefinitionsYou')}</strong> {t('DefinitionsYouText')}
						</p>
					</li>
				</ul>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('DataCollectionTitle')}</h2>
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('DataCollectionTypes')}</h3>
				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DataCollectionPersonalData')}</h4>
				<p className="mb-4">{t('DataCollectionPersonalDataText')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2"><p>{t('DataCollectionPersonalDataItemsEmail')}</p></li>
					<li className="mb-2"><p>{t('DataCollectionPersonalDataItemsName')}</p></li>
					<li className="mb-2"><p>{t('DataCollectionPersonalDataItemsUsageData')}</p></li>
				</ul>

				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DataCollectionUsageData')}</h4>
				<p className="mb-4">{t('DataCollectionUsageDataText1')}</p>
				<p className="mb-4">{t('DataCollectionUsageDataText2')}</p>
				<p className="mb-4">{t('DataCollectionUsageDataText3')}</p>
				<p className="mb-4">{t('DataCollectionUsageDataText4')}</p>

				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DataCollectionTracking')}</h4>
				<p className="mb-4">{t('DataCollectionTrackingText')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2">
						<strong
							className="text-gray-900">{t('DataCollectionTrackingItemsCookies')}</strong> {t('DataCollectionTrackingItemsCookiesText')}
					</li>
					<li className="mb-2">
						<strong
							className="text-gray-900">{t('DataCollectionTrackingItemsWebBeacons')}</strong> {t('DataCollectionTrackingItemsWebBeaconsText')}
					</li>
				</ul>
				<p className="mb-4">
					{t('DataCollectionCookiesDescription')}{' '}
					<Link
						to="https://www.privacypolicies.com/blog/privacy-policy-template/#Use_Of_Cookies_Log_Files_And_Tracking"
						target="_blank"
						className="text-blue-600 hover:text-red-500 hover:underline"
					>
						{t('DataCollectionCookiesLink')}
					</Link>
				</p>
				<p className="mb-4">{t('DataCollectionCookiesUsage')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2">
						<p><strong className="text-gray-900">{t('DataCollectionCookiesTypesNecessary')}</strong></p>
						<p>{t('DataCollectionCookiesTypesNecessaryType')}</p>
						<p>{t('DataCollectionCookiesTypesNecessaryAdmin')}</p>
						<p>{t('DataCollectionCookiesTypesNecessaryPurpose')}</p>
					</li>
					<li className="mb-2">
						<p><strong className="text-gray-900">{t('DataCollectionCookiesTypesAcceptance')}</strong></p>
						<p>{t('DataCollectionCookiesTypesAcceptanceType')}</p>
						<p>{t('DataCollectionCookiesTypesAcceptanceAdmin')}</p>
						<p>{t('DataCollectionCookiesTypesAcceptancePurpose')}</p>
					</li>
					<li className="mb-2">
						<p><strong className="text-gray-900">{t('DataCollectionCookiesTypesFunctionality')}</strong></p>
						<p>{t('DataCollectionCookiesTypesFunctionalityType')}</p>
						<p>{t('DataCollectionCookiesTypesFunctionalityAdmin')}</p>
						<p>{t('DataCollectionCookiesTypesFunctionalityPurpose')}</p>
					</li>
				</ul>
				<p>{t('DataCollectionCookiesMoreInfo')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('DataUseTitle')}</h3>
				<p className="mb-4">{t('DataUseDescription')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesService')}</strong> {t('DataUsePurposesServiceText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesAccount')}</strong> {t('DataUsePurposesAccountText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesContract')}</strong> {t('DataUsePurposesContractText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesContact')}</strong> {t('DataUsePurposesContactText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesOffers')}</strong> {t('DataUsePurposesOffersText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesRequests')}</strong> {t('DataUsePurposesRequestsText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesTransfers')}</strong> {t('DataUsePurposesTransfersText')}
					</p></li>
					<li className="mb-2"><p><strong
						className="text-gray-900">{t('DataUsePurposesOther')}</strong> {t('DataUsePurposesOtherText')}
					</p></li>
				</ul>
				<p className="mb-4">{t('DataUseSharing')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsProviders')}</strong> {t('DataUseSharingItemsProvidersText')}
					</li>
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsTransfers')}</strong> {t('DataUseSharingItemsTransfersText')}
					</li>
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsAffiliates')}</strong> {t('DataUseSharingItemsAffiliatesText')}
					</li>
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsPartners')}</strong> {t('DataUseSharingItemsPartnersText')}
					</li>
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsUsers')}</strong> {t('DataUseSharingItemsUsersText')}
					</li>
					<li className="mb-2"><strong
						className="text-gray-900">{t('DataUseSharingItemsConsent')}</strong> {t('DataUseSharingItemsConsentText')}
					</li>
				</ul>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('RetentionTitle')}</h3>
				<p className="mb-4">{t('RetentionText1')}</p>
				<p>{t('RetentionText2')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('TransferTitle')}</h3>
				<p className="mb-4">{t('TransferText1')}</p>
				<p className="mb-4">{t('TransferText2')}</p>
				<p>{t('TransferText3')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('DeleteTitle')}</h3>
				<p className="mb-4">{t('DeleteText1')}</p>
				<p className="mb-4">{t('DeleteText2')}</p>
				<p className="mb-4">{t('DeleteText3')}</p>
				<p>{t('DeleteText4')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('DisclosureTitle')}</h3>
				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DisclosureBusiness')}</h4>
				<p className="mb-4">{t('DisclosureBusinessText')}</p>

				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DisclosureLaw')}</h4>
				<p className="mb-4">{t('DisclosureLawText')}</p>

				<h4 className="text-lg text-gray-600 mt-0 mb-2">{t('DisclosureOther')}</h4>
				<p className="mb-4">{t('DisclosureOtherText')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2">{t('DisclosureOtherItemsLegal')}</li>
					<li className="mb-2">{t('DisclosureOtherItemsRights')}</li>
					<li className="mb-2">{t('DisclosureOtherItemsWrongdoing')}</li>
					<li className="mb-2">{t('DisclosureOtherItemsSafety')}</li>
					<li className="mb-2">{t('DisclosureOtherItemsLiability')}</li>
				</ul>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h3 className="text-xl text-gray-800 mt-0 mb-2">{t('SecurityTitle')}</h3>
				<p>{t('SecurityText')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('ChildrenTitle')}</h2>
				<p className="mb-4">{t('ChildrenText1')}</p>
				<p>{t('ChildrenText2')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('LinksTitle')}</h2>
				<p className="mb-4">{t('LinksText1')}</p>
				<p>{t('LinksText2')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('ChangesTitle')}</h2>
				<p className="mb-4">{t('ChangesText1')}</p>
				<p className="mb-4">{t('ChangesText2')}</p>
				<p>{t('ChangesText3')}</p>
			</div>

			<div className="bg-white p-6 rounded-lg shadow-md mb-6">
				<h2 className="text-2xl text-[#1E90FF] mt-0 mb-4">{t('ContactTitle')}</h2>
				<p className="mb-4">{t('ContactText')}</p>
				<ul className="list-disc ml-6 mb-4">
					<li className="mb-2">
						{t('ContactLinkText')}{' '}
						<Link
							to="https://pingo.family/en/contact-us"
							rel="external nofollow noopener"
							target="_blank"
							className="text-blue-600 hover:text-red-500 hover:underline"
						>
							{`https://pingo.family/${i18n.language}/contact-us`}
						</Link>
					</li>
				</ul>
			</div>
		</div>
	);
};

export default PrivacyPolicy;
