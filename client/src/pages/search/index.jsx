import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useQueryParams } from '../../hooks/useQueryParams';

const Search = (props) => {
  const {} = props;
  const [queryParams] = useQueryParams();
  const [inputSearchValue, setInputSearchValue] = useState(queryParams.get('query') || '');

  return (
    <div>Search Page</div>
  );
};

Search.propTypes = {};

Search.defaultProps = {};

export default Search;
