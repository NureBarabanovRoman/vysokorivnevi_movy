const NodeCache = require('node-cache');
const cache = new NodeCache({ stdTTL: 60 });

module.exports = (req, res, next) => {
  if (req.method !== 'GET') {
    return next();
  }

  const key = req.originalUrl;
  const cachedResponse = cache.get(key);

  if (cachedResponse) {
    console.log(`Cache hit for ${key}`);
    return res.json(cachedResponse);
  } else {
    console.log(`Cache miss for ${key}`);
    res.originalJson = res.json;
    res.json = (body) => {
      cache.set(key, body);
      res.originalJson(body);
    };
    next();
  }
};

module.exports.cache = cache;
