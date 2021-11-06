import React, { useCallback, useState } from 'react';
import InputSearch from '../../components/inputSearch';
import { inputSearchHistory } from '../../mocks/inputSearch';

const UiKit = () => {
  // TODO: вынести в класс с историей поиска
  const [inputSearchHistoryQueries, setInputSearchHistoryQueries] = useState(inputSearchHistory);

  const handleOnEnterDown = useCallback((query) => {
    const newQueries = [...inputSearchHistoryQueries];
    const index = newQueries.indexOf(query);
    if (index >= -1) {
      newQueries.splice(index, 1);
    }

    newQueries.unshift(query);
    setInputSearchHistoryQueries(newQueries);
  }, [inputSearchHistoryQueries]);

  const handleOnDeleteHistoryItem = useCallback((query) => {
    const newQueries = [...inputSearchHistoryQueries];
    const index = newQueries.indexOf(query);
    if (index >= -1) {
      newQueries.splice(index, 1);
      setInputSearchHistoryQueries(newQueries);
    }
  }, [inputSearchHistoryQueries]);

  return (
    <div className="ui-kit">
      <div className="ui-kit__component">
        <InputSearch
          onEnterDown={handleOnEnterDown}
          onDeleteHistoryItem={handleOnDeleteHistoryItem}
          history={inputSearchHistoryQueries}
        />
      </div>
    </div>
  );
};

export default UiKit;
