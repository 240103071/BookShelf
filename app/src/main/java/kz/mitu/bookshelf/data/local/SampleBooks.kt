package kz.mitu.bookshelf.data.local

import kz.mitu.bookshelf.data.model.Book
import kz.mitu.bookshelf.data.model.BookStatus

val sampleBooks: List<Book> = listOf(
    Book(
        id = 1,
        title = "The Path of Abai",
        author = "Mukhtar Auezov",
        genre = "Historical novel",
        pages = 1100,
        rating = 5,
        status = BookStatus.FINISHED,
        description = "An epic about the life of the poet Abai Kunanbayev and the Kazakh steppe in the 19th century."
    ),
    Book(
        id = 2,
        title = "The Nomads",
        author = "Ilyas Yesenberlin",
        genre = "Historical novel",
        pages = 960,
        rating = 4,
        status = BookStatus.READING,
        description = "A trilogy covering the history of the Kazakh Khanate from its founding to the 19th century."
    ),
    Book(
        id = 3,
        title = "Book of Words",
        author = "Abai Kunanbayev",
        genre = "Philosophy",
        pages = 160,
        rating = 0,
        status = BookStatus.WANT_TO_READ,
        description = "Forty-five short philosophical essays on morality, education and the Kazakh people."
    ),
    Book(
        id = 4,
        title = "1984",
        author = "George Orwell",
        genre = "Dystopia",
        pages = 328,
        rating = 5,
        status = BookStatus.FINISHED,
        description = "Winston Smith lives under the constant surveillance of Big Brother and starts to question the Party."
    ),
    Book(
        id = 5,
        title = "The Little Prince",
        author = "Antoine ",
        genre = "Fable",
        pages = 96,
        rating = 4,
        status = BookStatus.FINISHED,
        description = "A pilot stranded in the desert meets a young prince who has travelled from a tiny asteroid."
    ),
    Book(
        id = 6,
        title = "Crime and Punishment",
        author = "Fyodor Dostoevsky",
        genre = "Psychological novel",
        pages = 671,
        rating = 0,
        status = BookStatus.WANT_TO_READ,
        description = "A poor student in St. Petersburg commits a murder and struggles with guilt and conscience."
    )
)
