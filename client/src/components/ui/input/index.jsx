import React, { useCallback } from 'react';
import PropTypes from 'prop-types';

const Input = (props) => {
  const {type, value, placeholder, onChange, onEnterDown, onFocus, limits} = props;

  const handleOnKeyDown = useCallback((event) => {
    if (event.key === 'Enter') {
      onEnterDown();
    }
  }, [onEnterDown]);

  return (
    <div className="input">
      <input
        className="input__input input__input-text"
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        onKeyDown={handleOnKeyDown}
        onFocus={onFocus}
        {...limits}
      />
    </div>
  );
};

Input.propTypes = {
  type: PropTypes.string,
  value: PropTypes.string,
  placeholder: PropTypes.string,
  onChange: PropTypes.func,
  onEnterDown: PropTypes.func,
  onFocus: PropTypes.func,
  limits: PropTypes.object,
};

Input.defaultProps = {
  type: 'text',
  value: '',
  placeholder: '',
  onChange: () => {},
  onEnterDown: () => {},
  onFocus: () => {},
  limits: {},
};

export default Input;
