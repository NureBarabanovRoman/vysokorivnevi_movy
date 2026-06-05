const express = require('express');
const { Order, Product, User } = require('../models');
const auth = require('../middleware/auth');
const router = express.Router();

router.get('/', auth, async (req, res) => {
  const orders = await Order.findAll({ 
    where: { userId: req.userId },
    include: [Product, { model: User, attributes: ['username', 'email'] }]
  });
  res.json(orders);
});

router.post('/', auth, async (req, res) => {
  const { products, totalAmount } = req.body; // products is array of { id, quantity }
  const order = await Order.create({ userId: req.userId, totalAmount });
  
  if (products && products.length > 0) {
    for (const p of products) {
      await order.addProduct(p.id, { through: { quantity: p.quantity } });
    }
  }
  
  res.status(201).json(order);
});

router.put('/:id', auth, async (req, res) => {
  await Order.update(req.body, { where: { id: req.params.id, userId: req.userId } });
  res.json({ message: 'Updated' });
});

router.delete('/:id', auth, async (req, res) => {
  await Order.destroy({ where: { id: req.params.id, userId: req.userId } });
  res.json({ message: 'Deleted' });
});

module.exports = router;
