import React from 'react';
import PropTypes from 'prop-types';

const SearchButton = (props) => {
  const {text, onClick, disabled} = props;

  return (
    <button
      className='search-button'
      onClick={onClick}
      disabled={disabled}
    >
      {text}
    </button>
  );
};

SearchButton.propTypes = {
  text: PropTypes.string,
  onClick: PropTypes.func,
  disabled: PropTypes.bool,
};

SearchButton.defaultProps = {
  text: 'Search',
  onClick: () => {},
  disabled: false,
};

export default SearchButton;
