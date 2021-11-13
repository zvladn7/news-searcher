import React, { useMemo } from 'react';
import PropTypes from 'prop-types';
import { MOBILE_VIEW } from '../../../constants/view';
import SearchIcon from '../../../assets/svg/searchIcon';

const SimilarQueries = (props) => {
  const {title, queries, viewType, onClickItem} = props;

  const itemsCount = useMemo(() => viewType === MOBILE_VIEW ? 4 : 8, [viewType]);

  return (
    <div className="similar-queries">
      <span className="similar-queries__title">{title}</span>
      <div className="similar-queries__content">
        {queries.slice(0, itemsCount).map((query, order) => (
          <div
            className="similar-queries__item" key={`similar-queries__item-${order}`} onClick={() => onClickItem(query)}
          >
            <div className="similar-queries__item-icon">
              <SearchIcon />
            </div>
            <span className="similar-queries__item-text">{query}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

SimilarQueries.propTypes = {
  title: PropTypes.string,
  queries: PropTypes.arrayOf(PropTypes.string),
  viewType: PropTypes.string,
  onClickItem: PropTypes.func,
};

SimilarQueries.defaultProps = {
  title: 'Похожие поисковые запросы',
  queries: [],
  viewType: MOBILE_VIEW,
  onClickItem: () => {},
};

export default SimilarQueries;
