import React from 'react';
import PropTypes from 'prop-types';

const BlockResultSearch = (props) => {
  const {title, link} = props;

  return (
    <div className="block-result-search">
      <div className="block-result-search__title-wrapper">
        <a href={link} target="_blank" className="block-result-search__title">{title}</a>
      </div>
      <div className="block-result-search__bottom-bar">
        <div className="block-result-search__link-wrapper">
          <a href={link} target="_blank" className="block-result-search__link">{link}</a>
        </div>
      </div>
    </div>
  );
};

BlockResultSearch.propTypes = {
  title: PropTypes.string,
  link: PropTypes.string,
};

BlockResultSearch.defaultProps = {
  title: '',
  link: '',
};

export default BlockResultSearch;
