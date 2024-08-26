import React, { useEffect, useState, useContext } from 'react';
import Config from '../Config';
import AuthContext from '../context/AuthContext'; // Import AuthContext for token access
import './HomePage.css';

const HomePage = () => {
  const [products, setProducts] = useState([]);
  const { authTokens } = useContext(AuthContext); // Extract authTokens from AuthContext
  let baseURL = Config.baseURL;

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch(`${baseURL}/product/latest`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authTokens?.access}` // Attach the token in the request
          }
        });
        if (response.ok) {
          const data = await response.json();
          setProducts(data);
        } else {
          console.error('Failed to fetch products');
        }
      } catch (error) {
        console.error('There was an error fetching the products!', error);
      }
    };

    if (authTokens) {
      fetchProducts(); // Fetch products only when authTokens are available
    }
  }, [authTokens]);

  return (
    <div className="product-list-container">
      <h1 className="product-list-title">Latest Products</h1>
      <div className="product-grid">
        {products.map(product => (
          <div key={product.pId} className="product-card">
            <h2>{product.pName}</h2>
            <p>{product.pDesc}</p>
            <p>Base Price: {product.basePrice}</p>
            <p>Bid Duration: {product.bidDuration} hours</p>
            {product.imageUrls && product.imageUrls.length > 0 && (
              <img src={`${baseURL}${product.imageUrls[0]}`} alt={product.pName} />
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default HomePage;
