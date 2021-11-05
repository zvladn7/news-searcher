import React, { useCallback, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import Input from '../ui/input';
import SearchIcon from '../../assets/svg/searchIcon';
import CloseIcon from '../../assets/svg/closeIcon';
import ClockIcon from '../../assets/svg/clockIcon';
import TrashIcon from '../../assets/svg/trashIcon';
import { useOutsideClick } from '../../hooks/events';
import { inputSearchHistory } from '../../mocks/inputSearch';

const HISTORY_COUNT = 10;

const InputSearch = (props) => {
  const {placeholder, onEnterDown} = props;

  const ref = useRef(null);
  const [value, setValue] = useState('');
  const [queries, setQueries] = useState(inputSearchHistory);
  const [isFocus, setIsFocus] = useState(false);

  useOutsideClick(ref, () => setIsFocus(false));

  const handleOnFocusInput = () => setIsFocus(true);

  const handleOnChangeValue = (event) => setValue(event.target.value);

  const handleOnClickDeleteIcon = () => setValue('');

  const handleNewHistoryItem = useCallback((value) => {
    if (!value) {
      return;
    }

    const newQueries = [...queries];
    const index = newQueries.indexOf(value);
    if (index >= -1) {
      newQueries.splice(index, 1);
    }

    newQueries.unshift(value);
    setQueries(newQueries);
  }, [queries]);

  const handleOnEnterDown = useCallback(() => {
    onEnterDown(value);
    handleNewHistoryItem(value);
  }, [value, onEnterDown]);

  const handleOnClickHistoryItem = useCallback((query) => {
    setValue(query);
    handleNewHistoryItem(query);
  }, [onEnterDown]);

  const handleOnDeleteHistoryItem = useCallback((query) => {
    const newQueries = [...queries];
    const index = newQueries.indexOf(query);
    if (index >= -1) {
      newQueries.splice(index, 1);
      setQueries(newQueries);
    }
  }, [queries]);

  return (
    <div className="input-search">
      <div className="input-search__content-wrapper" ref={ref}>
        <div className="input-search__content">
          <div className="input-search__icon input-search__icon--indent-right">
            <SearchIcon />
          </div>
          <Input
            value={value}
            onChange={handleOnChangeValue}
            placeholder={placeholder}
            onEnterDown={handleOnEnterDown}
            onFocus={handleOnFocusInput}
          />
          <div className="input-search__icon input-search__icon--click-effect" onClick={handleOnClickDeleteIcon}>
            <CloseIcon />
          </div>
        </div>
        {isFocus && !value && (
          <div className="input-search__history">
            {queries.slice(0, HISTORY_COUNT).map((query, order) => (
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
                  onClick={() => handleOnDeleteHistoryItem(query)}
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
  placeholder: PropTypes.string,
  onEnterDown: PropTypes.func,
};

InputSearch.defaultProps = {
  placeholder: 'Поиск в новостях...',
  onEnterDown: () => {},
};

export default InputSearch;
