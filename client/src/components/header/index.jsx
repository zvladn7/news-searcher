import React, { useCallback } from 'react';
import PropTypes from 'prop-types';
import {
  ENGLISH_LANGUAGE,
  RUSSIAN_LANGUAGE,
  searchButtonText,
  searchInputPlaceholder, tabImage,
  tabText,
} from '../../constants/language';
import InputSearch from '../inputSearch';
import SearchButton from '../ui/searchButton';
import LanguageButton from '../ui/languageButton';
import { useWindowSize } from '../../hooks/useWindowSize';
import classNames from 'classnames';
import { TABLET_VIEW } from '../../constants/view';
import Tabs from '../ui/tabs';

const Header = (props) => {
  const {
    searchValue,
    onChangeSearchValue,
    queriesHistory,
    deleteQueryFromHistory,
    onSearch,
    language,
    changeLanguage,
    tabSelected,
    onChangeTab,
  } = props;

  const windowSize = useWindowSize();
  const {viewType} = windowSize;

  const handleChangeLanguage = useCallback(() => {
    changeLanguage(language === RUSSIAN_LANGUAGE ? ENGLISH_LANGUAGE : RUSSIAN_LANGUAGE);
  }, [language, changeLanguage]);

  const handleOnClickSearchButton = useCallback(() => {
    onSearch(searchValue);
  }, [searchValue, onSearch]);

  return (
    <div className="header">
      <div className="header__content">
        <span className="header__title">Search</span>
        <div className="header__search-input-wrapper">
          <InputSearch
            value={searchValue}
            onChange={onChangeSearchValue}
            placeholder={searchInputPlaceholder[language]}
            history={queriesHistory}
            onDeleteHistoryItem={deleteQueryFromHistory}
            onEnterDown={onSearch}
          />
        </div>
        <div
          className={classNames('header__buttons-group', {
            'header__buttons-group--absolute': viewType === TABLET_VIEW,
          })}
        >
          <div className="header__buttons-group-item">
            <SearchButton
              text={searchButtonText[language]} onClick={handleOnClickSearchButton} disabled={!searchValue}
            />
          </div>
          <div className="header__buttons-group-item">
            <LanguageButton type={language} onClick={handleChangeLanguage} />
          </div>
        </div>
      </div>
      <div className="header__tabs">
        <Tabs
          tabs={[
            {
              id: 0,
              title: tabText[language],
            },
            {
              id: 1,
              title: tabImage[language],
            },
          ]}
          onTabClick={onChangeTab}
          selectedTabId={tabSelected}
        />
      </div>
    </div>
  );
};

Header.propTypes = {
  searchValue: PropTypes.string,
  onChangeSearchValue: PropTypes.func,
  queriesHistory: PropTypes.arrayOf(PropTypes.string),
  deleteQueryFromHistory: PropTypes.func,
  onSearch: PropTypes.func,
  language: PropTypes.string,
  changeLanguage: PropTypes.func,
  tabSelected: PropTypes.number,
  onChangeTab: PropTypes.func,
};

Header.defaultProps = {
  searchValue: '',
  onChangeSearchValue: () => {},
  queriesHistory: [],
  deleteQueryFromHistory: () => {},
  onSearch: () => {},
  language: RUSSIAN_LANGUAGE,
  changeLanguage: () => {},
  tabSelected: 0,
  onChangeTab: () => {},
};

export default Header;
