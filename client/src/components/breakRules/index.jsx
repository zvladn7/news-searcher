import React from 'react';
import PropTypes from 'prop-types';
import {
  resultErrorFirstPart,
  resultErrorSecondPart,
  rule1,
  rule2,
  RUSSIAN_LANGUAGE,
  searchQueryRulesTitle,
} from '../../constants/language';
import TermsBlock from '../ui/termsBlock';

const BreakRules = (props) => {
  const {searchQuery, breakRules, language} = props;

  return (
    <div className="break-rules">
      <span className="break-rules__warning">
        {resultErrorFirstPart[language]}{' '}
        {!!searchQuery && (
          <>
            {'- '}<span className="break-rules__query">{searchQuery}</span>{' - '}
          </>
        )}
        {resultErrorSecondPart[language]}
      </span>
      <div className="break-rules__list-rules">
        {breakRules.map((rule, index) => (
          <div className="break-rules__list-rules-item" key={`break-rules__list-rules-item-${index}`}>
            {rule[language]}
          </div>
        ))}
      </div>
      <div className="not-found__suggestions">
        <TermsBlock
          terms={[
            rule1[language],
            rule2[language],
          ]}
          title={searchQueryRulesTitle[language]}
        />
      </div>
    </div>
  );
};

BreakRules.propTypes = {
  searchQuery: PropTypes.string,
  breakRules: PropTypes.array,
  language: PropTypes.string,
};

BreakRules.defaultProps = {
  searchQuery: '',
  breakRules: [],
  language: RUSSIAN_LANGUAGE,
};

export default BreakRules;
