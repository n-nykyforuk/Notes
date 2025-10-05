import React, { useState } from 'react';

const NoteForm = ({ onSubmit, initialTitle = '', initialContent = '' }) => {
  const [title, setTitle] = useState(initialTitle);
  const [content, setContent] = useState(initialContent);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ title, content });
    setTitle('');
    setContent('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Заголовок"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        required
      />
      <textarea
        placeholder="Зміст"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        required
      />
      <button type="submit">Зберегти</button>
    </form>
  );
};

export default NoteForm;