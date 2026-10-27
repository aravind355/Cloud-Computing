from flask import Flask
from flask import send_file
from flask import request
import os
import json
import requests

app = Flask(__name__)

# The folder where this program will save the book files.
# Book names should be in the following format: "pg"+Gutenberg_ID.txt - or as an example: "pg12345.txt"

book_folder = "./books" #changed becoz i have them in same folder


# Python method definition that implements a HTTP GET method
# app.route(REST_ENDPOINT, list_of_methods) determines which HTTP requests are handled by this method
# In this case: sending a GET method to the REST endpoint "/books" is handled by this method named list_of_books().
# When a GET request is sent to "http://localhost:5000/books", this method will be executed.
# The data that this method returnes will be provided as a response to the API call.
@app.route('/books', methods=['GET'])
def list_of_books():
    # This method should return a JSON object, that contains a list of books
    # Lets first initiate an empty list
    books = []

    # This is the structure of the output JSON
    # Your goal is to implement the logic to populate the list of books based on the content of the book folder

    for file_name in os.listdir(book_folder):
       if file_name.endswith(".txt"):
           books.append(file_name.split(".")[0][5:]) #here i am cutting 5 letters because in my downloads it is book_45524.txt not pg

    # As a result of the request, we return a JSON (python dictionary) object and HTTP code 200 (OK)
    return {"books": books}, 200

@app.route('/books/<book_id>', methods=['GET'])
def download_book(book_id):
    if not book_id.isdigit():#this checks whether it is integer or not
        return {"error": "Invalid book ID. Must be an integer."}, 400

    file_name = f"book_{book_id}.txt"
    file_path = os.path.join(book_folder, file_name)

    if not os.path.exists(file_path):#if there is no book with given id
        return {"error": "Book not found."}, 404

    return send_file(file_path, as_attachment=False)

@app.route('/books/<book_id>', methods=['DELETE'])
def delete_book(book_id):
    if not book_id.isdigit():#this checks whether it is integer or not
        return {"error": "Invalid book ID. Must be an integer."}, 400

    file_name = f"book_{book_id}.txt"
    file_path = os.path.join(book_folder, file_name)

    if not os.path.exists(file_path):#if there is no book with given id
        return {"error": "Book not found."}, 404

    os.remove(file_path)
    return {"message": f"Book_{book_id}.txt deleted successfully."}, 200

@app.route('/books', methods=['POST'])
def add_book():
    data = request.get_json()

    if not data or "book_id" not in data:#if input is not given
        return {"error": "book_id is required"}, 400

    book_id = data["book_id"]

    if not str(book_id).isdigit():#this checks whether it is integer or not
        return {"error": "Invalid book ID. Must be an integer."}, 400

    file_name = f"book_{book_id}.txt"
    file_path = os.path.join(book_folder, file_name)

    if os.path.exists(file_path): #this checks the file if it is already there
        return {"message": f"Book {book_id} already exists."}, 200

    url = f"https://www.gutenberg.org/cache/epub/{book_id}/pg{book_id}.txt"

    try:
        response = requests.get(url)

        if response.status_code != 200:
            return {"error": "Failed to download book from Gutenberg."}, 404

        with open(file_path, "w", encoding="utf-8") as f:
            f.write(response.text)

        return {"message": f"Book_{book_id}.txt downloaded successfully."}, 201

    except Exception as e:
        return {"error": str(e)}, 500

@app.route('/word_search', methods=['POST'])
def search_term_from_book():
    data = request.get_json()

    if not data or "book_id" not in data or "search_term" not in data:#if inputs are not given
        return {"error": "book_id and search_term are required"}, 400

    book_id = data["book_id"]
    search_term = data["search_term"]

    if not str(book_id).isdigit():#this checks whether it is integer or not
        return {"error": "Invalid book ID. Must be an integer."}, 400

    file_name = f"book_{book_id}.txt"
    file_path = os.path.join(book_folder, file_name)

    if not os.path.exists(file_path):#if there is no book with given id
        return {"error": "Book not found."}, 404

    try:
        with open(file_path, "r", encoding="utf-8") as f: #this is case-sensitive
            content = f.read().lower()

        count = content.count(search_term.lower())

        return {
            "book_id": str(book_id),
            "occurrences": count,
            "search_term": search_term
        }, 200

    except Exception as e:
        return {"error": str(e)}, 500

# The main part of the Python script that starts a Flask application
if __name__ == '__main__':
    # We activate DEBUG mode (debug=True) to see additional information about the program's work in the output.
    # And we specify that Flask should run the API server on port 5000 (port=5000)
    app.run(debug=True, port=5000)
