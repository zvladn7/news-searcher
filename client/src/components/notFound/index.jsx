import React from 'react';
import PropTypes from 'prop-types';
import {
  resultErrorFirstPart,
  resultNotFoundSecondPart,
  RUSSIAN_LANGUAGE,
  suggestionsTitle,
  warningWrongSearchQuery,
  warningWrongSearchQuerySuggestion1,
  warningWrongSearchQuerySuggestion2,
  warningWrongSearchQuerySuggestion3,
} from '../../constants/language';
import TermsBlock from '../ui/termsBlock';

const NotFound = (props) => {
  const {searchQuery, language} = props;

  return (
    <div className="not-found">
      <span className="not-found__warning">
        {resultErrorFirstPart[language]}{' '}
        {!!searchQuery && (
          <>
            {'- '}<span className="not-found__query">{searchQuery}</span>{' - '}
          </>
        )}
        {resultNotFoundSecondPart[language]}
      </span>
      <div className="not-found__suggestions">
        <TermsBlock
          terms={[
            warningWrongSearchQuery[language],
            warningWrongSearchQuerySuggestion1[language],
            warningWrongSearchQuerySuggestion2[language],
            warningWrongSearchQuerySuggestion3[language],
          ]}
          title={suggestionsTitle[language]}
        />
      </div>
    </div>
  );
};

NotFound.propTypes = {
  searchQuery: PropTypes.string,
  language: PropTypes.string,
};

NotFound.defaultProps = {
  searchQuery: '',
  language: RUSSIAN_LANGUAGE,
};

export default NotFound;
