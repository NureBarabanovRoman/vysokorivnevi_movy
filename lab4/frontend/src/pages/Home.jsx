import React, { useState, useEffect } from 'react';
import api from '../api';

const Home = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await api.get('/products');
        setProducts(res.data);
      } catch (err) {
        console.error('Failed to fetch products');
      }
    };
    fetchProducts();
  }, []);

  return (
    <div>
      <h2>Products</h2>
      <div className="product-list">
        {products.length === 0 ? (
          <p>No products found. Add some in the database!</p>
        ) : (
          products.map((product) => (
            <div key={product.id} className="product-card">
              <h3>{product.name}</h3>
              <p>{product.description}</p>
              <p>Price: ${product.price}</p>
              <p>Category: {product.Category?.name || 'N/A'}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Home;
