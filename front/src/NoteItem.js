import React from 'react';
import moment from 'moment';

const NoteItem = ({ note, onDelete }) => {
  const formattedDate = moment(note.createdAt).format('DD/MM/YYYY HH:mm');

  return (
    <li className="note-item">
      <h3 className="note-title">{note.title || 'No title'}</h3>
      <p className="note-snippet">{note.content ? note.content.substring(0, 50) + '...' : 'No content'}</p>
      <p className="note-date">{formattedDate}</p>
      <button onClick={() => onDelete(note.id)}>Видалити</button>
    </li>
  );
};

export default NoteItem;