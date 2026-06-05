const sequelize = require('./db');
const { User, Category, Product, Review, Order } = require('./models');
const bcrypt = require('bcryptjs');

async function seed() {
  await sequelize.sync({ force: true });

  const hashedPassword = await bcrypt.hash('password123', 10);
  const user = await User.create({
    username: 'admin',
    email: 'admin@example.com',
    password: hashedPassword
  });

  const category = await Category.create({ name: 'Electronics' });

  const product = await Product.create({
    name: 'Laptop',
    price: 999.99,
    description: 'A powerful laptop',
    categoryId: category.id
  });

  await Review.create({
    rating: 5,
    comment: 'Great product!',
    userId: user.id,
    productId: product.id
  });

  const order = await Order.create({
    userId: user.id,
    totalAmount: 999.99,
    status: 'completed'
  });

  await order.addProduct(product, { through: { quantity: 1 } });

  console.log('Database seeded!');
  process.exit();
}

seed();
