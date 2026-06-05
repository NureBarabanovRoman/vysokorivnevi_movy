import React, { useState, useMemo } from 'react';

type UserGroup = 'ЦА' | 'Постійні клієнти' | 'Інші';

interface User {
  id: number;
  name: string;
  email: string;
  group: UserGroup;
}

const initialUsers: User[] = Array.from({ length: 25 }, (_, i) => ({
  id: i + 1,
  name: `Користувач ${i + 1}`,
  email: `user${i + 1}@example.com`,
  group: i % 3 === 0 ? 'ЦА' : i % 3 === 1 ? 'Постійні клієнти' : 'Інші',
}));

// парсер JSON (завдання 2)
const JsonParser: React.FC = () => {
  const [input, setInput] = useState('[\n  {"id": 1, "name": "Ivan", "role": "Admin"},\n  {"id": 2, "name": "Maria", "role": "User"}\n]');
  const [parsed, setParsed] = useState<any>(null);
  const [error, setError] = useState<string>('');

  const handleParse = () => {
    try {
      const result = JSON.parse(input);
      setParsed(result);
      setError('');
    } catch (e) {
      setError('Некоректний JSON-рядок');
      setParsed(null);
    }
  };

  const renderTable = (data: any) => {
    if (!data || typeof data !== 'object') return String(data);

    // масив об'єктів
    if (Array.isArray(data) && data.length > 0 && typeof data[0] === 'object') {
      const headers = Object.keys(data[0]);
      return (
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
          <thead>
            <tr style={{ backgroundColor: '#eee' }}>
              {headers.map(h => <th key={h} style={{ border: '1px solid #ccc', padding: '8px' }}>{h}</th>)}
            </tr>
          </thead>
          <tbody>
            {data.map((row, i) => (
              <tr key={i}>
                {headers.map(h => (
                  <td key={h} style={{ border: '1px solid #ccc', padding: '8px' }}>
                    {typeof row[h] === 'object' ? JSON.stringify(row[h]) : String(row[h])}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      );
    }

    // одиночний об'єкт
    const keys = Object.keys(data);
    return (
      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
        <thead>
          <tr style={{ backgroundColor: '#eee' }}>
            <th style={{ border: '1px solid #ccc', padding: '8px' }}>Властивість</th>
            <th style={{ border: '1px solid #ccc', padding: '8px' }}>Значення</th>
          </tr>
        </thead>
        <tbody>
          {keys.map(key => (
            <tr key={key}>
              <td style={{ border: '1px solid #ccc', padding: '8px', fontWeight: 'bold' }}>{key}</td>
              <td style={{ border: '1px solid #ccc', padding: '8px' }}>
                {typeof data[key] === 'object' ? JSON.stringify(data[key]) : String(data[key])}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  return (
    <div style={{ marginBottom: '40px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
      <h2>2. Парсер JSON</h2>
      <textarea
        value={input}
        onChange={(e) => {setInput(e.target.value); handleParse()}}
        rows={6}
        style={{ width: '100%', marginBottom: '10px', padding: '10px', fontFamily: 'monospace' }}
        placeholder="Введіть JSON тут..."
      />
      <button onClick={handleParse} style={{ padding: '8px 16px', cursor: 'pointer', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px' }}>
        Розпарсити у таблицю
      </button>

      {error && <p style={{ color: 'red', marginTop: '10px' }}>{error}</p>}
      {parsed && (
        <div style={{ marginTop: '20px' }}>
          <h4>Результат у вигляді таблиці:</h4>
          <div style={{ overflowX: 'auto' }}>
            {renderTable(parsed)}
          </div>
        </div>
      )}
    </div>
  );
};

// Завдання 3 та 7
const App: React.FC = () => {
  const [users] = useState<User[]>(initialUsers);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);

  const totalPages = Math.ceil(users.length / itemsPerPage);
  const displayedUsers = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return users.slice(start, start + itemsPerPage);
  }, [users, currentPage, itemsPerPage]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleItemsPerPageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setItemsPerPage(Number(e.target.value));
    setCurrentPage(1);
  };

  return (
    <div style={{ fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
      <h1>React Практична робота 2</h1>

      {/* Завдання 2 */}
      <JsonParser />

      {/* Завдання 3 та 7 */}
      <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
        <h2>3 & 7. Список користувачів з пагінацією</h2>
        
        <div style={{ marginBottom: '15px' }}>
          <label>Елементів на сторінці: </label>
          <select value={itemsPerPage} onChange={handleItemsPerPageChange} style={{ padding: '5px' }}>
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={20}>20</option>
          </select>
        </div>

        <table style={{ width: '100%', borderCollapse: 'collapse', marginBottom: '20px' }}>
          <thead>
            <tr style={{ backgroundColor: '#f2f2f2' }}>
              <th style={{ border: '1px solid #ddd', padding: '12px', textAlign: 'left' }}>Ім'я</th>
              <th style={{ border: '1px solid #ddd', padding: '12px', textAlign: 'left' }}>Email</th>
              <th style={{ border: '1px solid #ddd', padding: '12px', textAlign: 'left' }}>Група</th>
            </tr>
          </thead>
          <tbody>
            {displayedUsers.map(user => (
              <tr key={user.id}>
                <td style={{ border: '1px solid #ddd', padding: '12px' }}>{user.name}</td>
                <td style={{ border: '1px solid #ddd', padding: '12px' }}>{user.email}</td>
                <td style={{ border: '1px solid #ddd', padding: '12px' }}>
                  <span style={{
                    padding: '4px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    backgroundColor: user.group === 'ЦА' ? '#e1f5fe' : user.group === 'Постійні клієнти' ? '#e8f5e9' : '#f5f5f5',
                    color: user.group === 'ЦА' ? '#01579b' : user.group === 'Постійні клієнти' ? '#1b5e20' : '#616161'
                  }}>
                    {user.group}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Пагінація */}
        <div style={{ display: 'flex', gap: '5px', justifyContent: 'center' }}>
          {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
            <button
              key={page}
              onClick={() => handlePageChange(page)}
              style={{
                padding: '8px 12px',
                cursor: 'pointer',
                backgroundColor: currentPage === page ? '#007bff' : '#fff',
                color: currentPage === page ? '#fff' : '#000',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              {page}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default App;
