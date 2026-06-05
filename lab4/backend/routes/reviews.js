const express = require('express');
const { Review, User, Product } = require('../models');
const auth = require('../middleware/auth');
const router = express.Router();

router.get('/product/:productId', async (req, res) => {
  const reviews = await Review.findAll({ 
    where: { productId: req.params.productId },
    include: [{ model: User, attributes: ['username'] }]
  });
  res.json(reviews);
});

router.post('/', auth, async (req, res) => {
  const review = await Review.create({ ...req.body, userId: req.userId });
  res.status(201).json(review);
});

router.put('/:id', auth, async (req, res) => {
  await Review.update(req.body, { where: { id: req.params.id, userId: req.userId } });
  res.json({ message: 'Updated' });
});

router.delete('/:id', auth, async (req, res) => {
  await Review.destroy({ where: { id: req.params.id, userId: req.userId } });
  res.json({ message: 'Deleted' });
});

module.exports = router;
