import React from 'react';
import PropTypes from 'prop-types';
import { LANGUAGE_BUTTON_ICONS, LANGUAGE_BUTTON_STRINGS } from './config';
import { ENGLISH_LANGUAGE } from '../../../constants/language';

const LanguageButton = (props) => {
  const {type, onClick, disabled} = props;

  const FlagIcon = LANGUAGE_BUTTON_ICONS[type] || LANGUAGE_BUTTON_ICONS.EN;

  return (
    <button
      className="language-button"
      onClick={onClick}
      disabled={disabled}
    >
      <div className="language-button__content">
        <div className="language-button__logo">
          <FlagIcon />
        </div>
        <span className="language-button__text">
          {LANGUAGE_BUTTON_STRINGS[type] || LANGUAGE_BUTTON_STRINGS.EN}
        </span>
      </div>
    </button>
  );
};

LanguageButton.propTypes = {
  type: PropTypes.string,
  onClick: PropTypes.func,
  disabled: PropTypes.bool,
};

LanguageButton.defaultProps = {
  type: ENGLISH_LANGUAGE,
  onClick: () => {},
  disabled: false,
};

export default LanguageButton;
