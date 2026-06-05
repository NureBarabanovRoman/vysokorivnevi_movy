import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [commentContent, setCommentContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchPost = async () => {
    try {
      const res = await api.get(`/posts/${id}`);
      setPost(res.data);
    } catch (err) {
      setError('Post not found');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!user) return navigate('/login');
    try {
      await api.post(`/posts/${id}/comments`, { content: commentContent });
      setCommentContent('');
      fetchPost();
    } catch (err) {
      alert('Failed to add comment');
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="post-detail">
      <article>
        <h1>{post.title}</h1>
        <div className="post-meta">
          By {post.author_name} | {new Date(post.created_at).toLocaleDateString()}
        </div>
        <div className="post-categories">
          {post.categories?.map(cat => (
            <span key={cat.id} className="category-tag">{cat.name}</span>
          ))}
        </div>
        <div className="post-content">
          {post.content}
        </div>
      </article>

      <section className="comments-section">
        <h3>Comments ({post.comments?.length || 0})</h3>
        {user ? (
          <form onSubmit={handleCommentSubmit} className="comment-form">
            <textarea
              placeholder="Write a comment..."
              value={commentContent}
              onChange={(e) => setCommentContent(e.target.value)}
              required
            ></textarea>
            <button type="submit" className="btn">Add Comment</button>
          </form>
        ) : (
          <p>Please <button onClick={() => navigate('/login')} className="btn-link">login</button> to comment.</p>
        )}

        <div className="comments-list">
          {post.comments?.map(comment => (
            <div key={comment.id} className="comment">
              <p><strong>{comment.author_name}</strong> • {new Date(comment.created_at).toLocaleString()}</p>
              <p>{comment.content}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default PostDetail;
