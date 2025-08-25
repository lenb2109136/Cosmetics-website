import React, { useState } from 'react';
import { Menu } from 'antd';
import {
  AppstoreOutlined,
  MailOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate } from 'react-router-dom';

const items = [
  {
    label: 'Quản lý sản phẩm',
    key: 'product-management',
    icon: <AppstoreOutlined />,
    children: [
      { label: 'Thêm sản phẩm', key: 'product/add' },
      { label: 'Tất cả sản phẩm', key: 'product' },
    ],
  },
  {
    label: 'Kênh marketing',
    key: 'marketing',
    icon: <AppstoreOutlined />,
    children: [
      { label: 'Flash sale của shop', key: 'marketing/flash' },
      { label: 'Deal của shop', key: 'marketing/deal' },
      // { label: 'Chương trình tặng kèm', key: 'marketing/bonus' },
    ],
  },
  {
    label: 'Quản lý nhà cung cấp',
    key: 'supplier/addsupplier',
    children: [
      { label: 'Thêm nhà cung cấp', key: 'supplier/addsupplier/0' },
      { label: 'Tất cả nhà cung cấp', key: 'supplier' },
    ],
    icon: <AppstoreOutlined />,
  },
  {
    label: 'Quản lý Xuất nhập kho',
    key: 'warehouse/import',
    icon: <AppstoreOutlined />,
  }
  , {
    label: 'Quản lý Hao hụt hàng hóa',
    key: 'haohut',
    icon: <AppstoreOutlined />,
  }
  ,
  {
    label: 'Quản lý tổng hợp',
    key: 'general/category',
    icon: <AppstoreOutlined />,
  }
  
];

const VerticalMenu = () => {
  const navigate = useNavigate();
  const [current, setCurrent] = useState('mail');
  const [isCollapsed, setIsCollapsed] = useState(false);

  const onClick = (e) => {
    console.log('click ', e);
    setCurrent(e.key);
    navigate(e.key);
  };

  const toggleNavbar = () => {
    setIsCollapsed(!isCollapsed);
  };

  return (
    <>
      <div className="h-24 bg-blue-500 flex items-center justify-between px-4">
        <h1 className="text-white text-xl font-bold">
          {isCollapsed ? 'Menu' : 'Quản lý Shop'}
        </h1>
        <button
          onClick={toggleNavbar}
          className="p-2 text-white rounded-full hover:bg-blue-600 focus:outline-none"
        >
          {isCollapsed ? '→' : '←'}
        </button>
      </div>
      <div className="flex">
        <div
          className={`bg-white transition-all duration-300 ease-in-out ${isCollapsed ? 'w-16' : 'w-64'
            }`}
        >
          <Menu
            onClick={onClick}
            selectedKeys={[current]}
            mode="inline"
            items={items}
            inlineCollapsed={isCollapsed}
            className="h-full"
          />
        </div>
        <div className="flex-1 h-screen overflow-y-auto pl-2 pr-2 bg-gray-50">
          <Outlet />
        </div>
      </div>
    </>
  );
};

export default VerticalMenu;