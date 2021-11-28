import { useCallback, useState } from 'react';
import { RUSSIAN_LANGUAGE } from '../constants/language';

const LOCAL_STORAGE_KEY = 'news-searcher-language';

const getFromLocalStorage = (key = LOCAL_STORAGE_KEY) => {
  return JSON.parse(localStorage.getItem(key));
};

const setToLocalStorage = (key = LOCAL_STORAGE_KEY, value = RUSSIAN_LANGUAGE) => {
  localStorage.setItem(key, JSON.stringify(value));
};

export const useLanguage = () => {
  const [language, setLanguage] = useState(getFromLocalStorage(LOCAL_STORAGE_KEY) || RUSSIAN_LANGUAGE);

  const changeLanguage = useCallback((language = RUSSIAN_LANGUAGE) => {
    setLanguage(language);
    setToLocalStorage(LOCAL_STORAGE_KEY, language);
  }, [setLanguage]);

  return [language, changeLanguage];
};
