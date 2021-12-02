import React from 'react';
import PropTypes from 'prop-types';
import { aboutResultsTextFirstPart, aboutResultsTextSecondPart, RUSSIAN_LANGUAGE } from '../../../constants/language';

const SearchCount = (props) => {
  const {language, resultCount} = props;

  return (
    <span className="search-count">
      {`${aboutResultsTextFirstPart[language]} ${resultCount.toLocaleString()} ${aboutResultsTextSecondPart[language]}`}
    </span>
  );
};

SearchCount.propTypes = {
  language: PropTypes.string,
  resultCount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
};

SearchCount.defaultProps = {
  language: RUSSIAN_LANGUAGE,
  resultCount: 0,
};

export default SearchCount;
