import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';

const Home = () => {
  const [posts, setPosts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const res = await api.get('/posts', {
        params: { search, category: selectedCategory }
      });
      setPosts(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await api.get('/categories');
      setCategories(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchPosts();
    }, 300);
    return () => clearTimeout(timer);
  }, [search, selectedCategory]);

  return (
    <div className="home-page">
      <div className="filters">
        <input 
          type="text" 
          placeholder="Search posts..." 
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="search-input"
        />
        <select 
          value={selectedCategory} 
          onChange={(e) => setSelectedCategory(e.target.value)}
          className="category-filter"
        >
          <option value="">All Categories</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <p>Loading posts...</p>
      ) : (
        <div className="post-list">
          {posts.length === 0 ? (
            <p>No posts found.</p>
          ) : (
            posts.map(post => (
              <article key={post.id} className="post-card">
                <h3><Link to={`/post/${post.id}`}>{post.title}</Link></h3>
                <p className="post-meta">
                  By {post.author_name} | {new Date(post.created_at).toLocaleDateString()}
                  {post.category_names && ` | ${post.category_names}`}
                </p>
                <p>{post.content.substring(0, 150)}...</p>
                <Link to={`/post/${post.id}`} className="read-more">Read More</Link>
              </article>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default Home;
