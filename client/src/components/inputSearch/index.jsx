import React, { useCallback, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import Input from '../ui/input';
import SearchIcon from '../../assets/svg/searchIcon';
import CloseIcon from '../../assets/svg/closeIcon';
import ClockIcon from '../../assets/svg/clockIcon';
import TrashIcon from '../../assets/svg/trashIcon';
import { useOutsideClick } from '../../hooks/events';

const HISTORY_COUNT = 10;

const InputSearch = (props) => {
  const {value, onChange, placeholder, history, onEnterDown, onDeleteHistoryItem} = props;

  const componentRef = useRef(null);
  const [isFocus, setIsFocus] = useState(false);

  useOutsideClick(componentRef, () => setIsFocus(false));

  const handleOnFocusInput = () => setIsFocus(true);

  const handleOnEnterDown = useCallback(() => {
    if (!value) {
      return;
    }

    onEnterDown(value);
  }, [value, onEnterDown]);

  const handleOnClickHistoryItem = useCallback((query) => {
    if (!query) {
      return;
    }

    onChange(query);
    onEnterDown(query);
  }, [onEnterDown]);

  return (
    <div className="input-search">
      <div className="input-search__content-wrapper" ref={componentRef}>
        <div className="input-search__content">
          <div className="input-search__icon input-search__icon--indent-right">
            <SearchIcon />
          </div>
          <Input
            value={value}
            onChange={(event) => onChange(event.target.value)}
            placeholder={placeholder}
            onEnterDown={handleOnEnterDown}
            onFocus={handleOnFocusInput}
          />
          <div className="input-search__icon input-search__icon--click-effect" onClick={() => onChange('')}>
            <CloseIcon />
          </div>
        </div>
        {isFocus && !value && (
          <div className="input-search__history">
            {history.slice(0, HISTORY_COUNT).map((query, order) => (
              <div className="input-search__history-item" key={`input-search__history-ite__${order}`}>
                <div className="input-search__history-item-icon">
                  <ClockIcon />
                </div>
                <span
                  className="input-search__history-item-text"
                  onClick={() => handleOnClickHistoryItem(query)}
                >
                  {query}
                </span>
                <div
                  className="input-search__history-item-icon input-search__history-item-icon--trash"
                  onClick={() => onDeleteHistoryItem(query)}
                >
                  <TrashIcon />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

InputSearch.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func,
  placeholder: PropTypes.string,
  history: PropTypes.arrayOf(PropTypes.string),
  onEnterDown: PropTypes.func,
  onDeleteHistoryItem: PropTypes.func,
};

InputSearch.defaultProps = {
  value: '',
  onChange: () => {},
  placeholder: 'Поиск в новостях...',
  history: [],
  onEnterDown: () => {},
  onDeleteHistoryItem: () => {},
};

export default InputSearch;
