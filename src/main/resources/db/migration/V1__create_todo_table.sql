CREATE TABlE Todos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title text,
    description text,
    status INT CHECK (status IN (0, 1))
)