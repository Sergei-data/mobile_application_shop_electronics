FROM python:3.11-slim

WORKDIR /app

# 1) зависимости
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -U pip \
    && pip install --no-cache-dir -r /app/requirements.txt

# 2) alembic + приложение
COPY alembic.ini /app/alembic.ini
COPY alembic /app/alembic
COPY app /app/app

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]