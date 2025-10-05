import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BrowserRouter as Router, Route, Routes, useNavigate, useParams } from 'react-router-dom';
import moment from 'moment';
import './App.css';

function App() {
  const [notes, setNotes] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchNotes();
  }, []);

  const fetchNotes = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/notes');
      const filteredNotes = response.data.filter(note => note.title || note.content);
      setNotes(filteredNotes);
    } catch (error) {
      console.error('Error loading notes:', error);
    }
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this note?')) {
      axios.delete(`http://localhost:8080/api/notes/${id}`)
        .then(() => {
          setNotes(notes.filter(note => note.id !== id));
        })
        .catch(error => console.error('Error deleting note:', error));
    }
  };

  const handleViewEdit = (id) => {
    navigate(`/note/${id}`);
  };

  const handleSearch = async (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    if (query) {
      try {
        const response = await axios.get(`http://localhost:8080/api/notes/search?query=${query}`);
        const filteredNotes = response.data.filter(note => note.title || note.content);
        setNotes(filteredNotes);
      } catch (error) {
        console.error('Error searching notes:', error);
      }
    } else {
      fetchNotes();
    }
  };

  const handleNewNote = () => {
    navigate('/note/new');
  };

  return (
    <div className="app-container">
      <h1>Notes</h1>
      <div className="navbar">
        <input
          type="text"
          value={searchQuery}
          onChange={handleSearch}
          placeholder="Search by title or content..."
          className="search-input"
        />
      </div>
      <ul className="note-list">
        {notes.map(note => (
          <li key={note.id} className="note-item">
            <div className="note-header">
              <h3 className="note-title" onClick={() => handleViewEdit(note.id)}>{note.title || ''}</h3>
              <span className="note-date">{moment(note.createdAt).format('DD/MM/YY')}</span>
            </div>
            <p className="note-content">{note.content ? note.content.substring(0, 50) + '...' : ''}</p>
            <button className="delete-button" onClick={() => handleDelete(note.id)}>
              🗑️
            </button>
          </li>
        ))}
      </ul>
      <button className="add-button" onClick={handleNewNote}>+</button>
    </div>
  );
}

// Component for editing/creating a note
function NoteDetail() {
  const { id } = useParams();
  const [editedNote, setEditedNote] = useState('');
  const [checklist, setChecklist] = useState([]);
  const [files, setFiles] = useState([]);
  const navigate = useNavigate();
  const [isNew, setIsNew] = useState(id === 'new');

  useEffect(() => {
    if (id !== 'new') {
      axios.get(`http://localhost:8080/api/notes/${id}`)
        .then(response => {
          const { title, content, checklist, files } = response.data;
          setEditedNote(`${title || ''}\n${content || ''}`);
          setChecklist(checklist || []);
          setFiles(files || []);
        })
        .catch(error => console.error('Error loading note:', error));
    } else {
      setEditedNote('');
      setChecklist([]);
      setFiles([]);
    }
  }, [id]);

  const handleBack = () => {
    const lines = editedNote.split('\n');
    const newTitle = lines[0] || '';
    const newContent = lines.slice(1).join('\n').trim() || '';
    const filteredChecklist = checklist.filter(item => item.text.trim() !== '');
    const noteData = {
      title: newTitle,
      content: newContent,
      checklist: filteredChecklist
    };
    if (newTitle === '' && newContent === '' && filteredChecklist.length === 0 && files.length === 0) {
      if (!isNew && id !== 'new') {
        axios.delete(`http://localhost:8080/api/notes/${id}`)
          .then(() => {
            navigate('/');
          })
          .catch(error => console.error('Error deleting empty note:', error));
      } else {
        navigate('/');
      }
    } else {
      if (isNew) {
        axios.post('http://localhost:8080/api/notes/create', noteData, {
          headers: { 'Content-Type': 'application/json' }
        })
          .then(response => {
            navigate('/');
          })
          .catch(error => console.error('Error creating note:', error));
      } else {
        axios.put(`http://localhost:8080/api/notes/${id}`, noteData, {
          headers: { 'Content-Type': 'application/json' }
        })
          .then(() => {
            navigate('/');
          })
          .catch(error => console.error('Error updating note:', error));
      }
    }
  };

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    selectedFiles.forEach(file => {
      const formData = new FormData();
      formData.append('file', file);
      axios.post(`http://localhost:8080/api/notes/${id}/attach`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
        .then(response => {
          setFiles(response.data.files || []);
        })
        .catch(error => console.error('Error attaching file:', error));
    });
  };

  const handleDownload = (fileId, fileName) => {
    window.open(`http://localhost:8080/api/notes/${id}/file/${fileId}`, '_blank');
  };

  const handleDeleteFile = (fileId) => {
    if (window.confirm('Are you sure you want to delete this file?')) {
      axios.delete(`http://localhost:8080/api/notes/${id}/file/${fileId}`)
        .then(() => {
          setFiles(prevFiles => prevFiles.filter(file => file.id !== fileId));
        })
        .catch(error => console.error('Error deleting file:', error));
    }
  };

  const handleExportPdf = () => {
    axios.get(`http://localhost:8080/api/notes/${id}/export`, { responseType: 'blob' })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `note_${id}.pdf`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      })
      .catch(error => console.error('Error exporting PDF:', error));
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this note?')) {
      axios.delete(`http://localhost:8080/api/notes/${id}`)
        .then(() => {
          navigate('/');
        })
        .catch(error => console.error('Error deleting note:', error));
    }
  };

  const addChecklistItem = () => {
    setChecklist([...checklist, { text: '', checked: false }]);
  };

  const updateChecklistItem = (index, field, value) => {
    const newChecklist = [...checklist];
    newChecklist[index][field] = value;
    setChecklist(newChecklist);
  };

  const removeChecklistItem = (index) => {
    const newChecklist = checklist.filter((_, i) => i !== index);
    setChecklist(newChecklist);
  };

  const triggerFileInput = () => {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.multiple = true; // Дозволяє вибір кількох файлів
    fileInput.style.display = 'none';
    fileInput.onchange = (e) => {
      handleFileChange(e);
      document.body.removeChild(fileInput);
    };
    document.body.appendChild(fileInput);
    fileInput.click();
  };

  return (
    <div className="app-container">
      <div className="note-detail-header">
        <button className="back-button" onClick={handleBack}>&lt; Notes</button>
      </div>
      <textarea
        value={editedNote}
        onChange={(e) => setEditedNote(e.target.value)}
        className="edit-textarea"
        autoFocus
      />
      <div className="checklist-section">
        {checklist.map((item, index) => (
          <div key={index} className="checklist-item">
            <input
              type="checkbox"
              checked={item.checked}
              onChange={(e) => updateChecklistItem(index, 'checked', e.target.checked)}
            />
            <input
              type="text"
              value={item.text}
              onChange={(e) => updateChecklistItem(index, 'text', e.target.value)}
              placeholder="Enter task..."
              className="checklist-input"
            />
            <button className="remove-button" onClick={() => removeChecklistItem(index)}>
              Remove
            </button>
          </div>
        ))}
      </div>
      <div className="file-attachment">
        {files.map(file => (
          <div key={file.id}>
            <p>Attached: {file.fileName}</p>
            <button onClick={() => handleDownload(file.id, file.fileName)}>Download</button>
            <button className="remove-button" onClick={() => handleDeleteFile(file.id)}>
              Remove
            </button>
          </div>
        ))}
      </div>
      {!isNew && (
        <button className="add-button delete-trash" onClick={() => handleDelete(id)}>
          🗑️
        </button>
      )}
      <button className="add-button checklist-button" onClick={addChecklistItem}>
        ☑️
      </button>
      <button className="add-button attachment-button" onClick={triggerFileInput}>
        📎
      </button>
      <button className="add-button export-button" onClick={handleExportPdf}>
        ↓
      </button>
    </div>
  );
}

// Routing setup
function AppWrapper() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/note/:id" element={<NoteDetail />} />
      </Routes>
    </Router>
  );
}

export default AppWrapper;