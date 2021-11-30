import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';

const Tabs = (props) => {
  const {tabs, onTabClick, selectedTabId} = props;

  return (
    <div className="tabs">
      {tabs.map((tab) => (
        <div
          key={`tabs-${tab.id}`}
          onClick={() => onTabClick(tab.id)}
          className={classNames('tabs__item', {
            'tabs__item--select': tab.id === selectedTabId,
          })}
        >
          <span className="tabs__item-title">{tab.title}</span>
        </div>
      ))}
    </div>
  );
};

Tabs.propTypes = {
  tabs: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      title: PropTypes.string,
    }),
  ),
  onTabClick: PropTypes.func,
  selectedTabId: PropTypes.number,
};

Tabs.defaultProps = {
  tabs: [],
  onTabClick: () => {},
  selectedTabId: 0,
};

export default Tabs;
