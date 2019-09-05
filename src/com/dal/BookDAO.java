/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dal;

import com.context.DBContext;
import com.entity.Author;
import com.entity.Book;
import com.entity.Publisher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class BookDAO {
    
    //delete a book by a given bookID
    public void deleteBook(String bookID) throws Exception {
        String insert = "delete from titleauthor where title_id = ?";
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1,bookID);
        ps.executeUpdate();
        ps.close();
        // secondly, delete from TitleAuthor table
        String delete = "delete from Books where title_id = ?";
        ps = conn.prepareStatement(delete);
        ps.setString(1,bookID);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }
    
    
    //update a new book to database
    public void editBook(Book b) throws Exception {
       String insert = "update Books set title = ?,pub_id = ?,notes = ? where title_id = ?";
       Connection conn = new DBContext().getConnection();
       PreparedStatement ps = conn.prepareStatement(insert);
       ps.setString(1,b.getTitle());
       ps.setString(2,b.getPub().getId());
       ps.setString(3,b.getNote());
       ps.setString(4,b.getId());
       ps.executeUpdate();
       ps.close();
       // secondly, update TitleAuthor table,remove all old authors of given book
       String delete = "delete from TitleAuthor where title_id = ?";
       ps = conn.prepareStatement(delete);
       ps.setString(1,b.getId());
       ps.executeUpdate(); ps.close();
       // update new authors of given book
       List<Author> authors = b.getAuthors();
       for(int i=0;i < authors.size();i++){
           Author a = authors.get(i);
           insert = "insert into TitleAuthor values(?,?,?)";
           PreparedStatement p = conn.prepareStatement(insert);
           p.setString(1, a.getId());
           p.setString(2, b.getId());
           p.setInt(3, i);
           p.executeUpdate();
           p.close();
       }
       conn.close();
    }
    
    
    //insert a new book to database
    public void addBook(Book b) throws Exception {
        String insert = "insert into Books values(?,?,?,?,?)";
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(insert);
        // specify the value for parameter
        ps.setString(1,b.getId());
        ps.setString(2,b.getTitle());
        ps.setString(3,b.getPub().getId());
        ps.setString(4,b.getNote());
        ps.setString(5,b.getUser().getUsername());
        ps.executeUpdate();
        ps.close();
        
        List<Author> authors = b.getAuthors();
        for(int i=0;i < authors.size();i++){
            Author a = authors.get(i);
            insert = "insert into TitleAuthor values(?,?,?)";
            PreparedStatement p = conn.prepareStatement(insert);
            p.setString(1, a.getId());
            p.setString(2, b.getId());
            p.setInt(3, i);
            p.executeUpdate();
            p.close();
        }
        conn.close();
    }
    
    //return information of a Book of a given bookID
    //return null if a given bookID does not exists
    public Book getBookByBookID(String bookID)throws Exception {
        String select = "select *from Books where title_id = ? ";
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setString(1, bookID);
        ResultSet rs = ps.executeQuery();
        // use to get information of a Publisher of the book
        PublisherDAO pubDAO = new PublisherDAO();
        // use to get information of a list of authors of the book
        AuthorDAO authorDAO = new AuthorDAO();
        Book b = null;
        if(rs.next()){
            String id = rs.getString("title_id");
            String title = rs.getString("title");
            String pubID = rs.getString("pub_id");
            String note = rs.getString("notes");
            // get Publisher of the book
            Publisher pub = pubDAO.getPublisherByID(pubID);
            // get list of authors of the book
            List<Author> authors = authorDAO.selectAuthorsByBookID(id);
            b = new Book(id, title,note,pub,authors);
        }
        rs.close();
        conn.close();
        return b;
    }
    
    //return the list of books - use for searching, need to join all given tables except Users
    public List<Book> select(String columnName, String keyword)throws Exception {
       String select = "select distinct Books.* from Books,Publishers,Authors,TitleAuthor where "+
               " books.pub_id = Publishers.pub_id and books.title_id = TitleAuthor.title_id and TitleAuthor.au_id = Authors.au_id AND ";
       select += columnName + " like '%"+keyword+"%'";
       
       Connection conn = new DBContext().getConnection();
       PreparedStatement ps = conn.prepareStatement(select);
       ResultSet rs = ps.executeQuery();
       List<Book> books = new ArrayList<>();
       
       PublisherDAO pubDAO = new PublisherDAO();
       AuthorDAO authorDAO = new AuthorDAO();
       while(rs.next()){
           String id = rs.getString("title_id");
           String title = rs.getString("title");
           String pubID = rs.getString("pub_id");
           String note = rs.getString("notes");
           // get Publisher of the book
           Publisher pub = pubDAO.getPublisherByID(pubID);
           // get list of authors of the book
           List<Author> authors = authorDAO.selectAuthorsByBookID(id);
           books.add(new Book(id, title, note, pub, authors));
       }
       rs.close();
       conn.close();
       return books;
       
    }
    
    //return the list of all books
    public List<Book> selectAll()throws Exception {
       String select = "select * from Books";
       Connection conn = new DBContext().getConnection();
       PreparedStatement ps = conn.prepareStatement(select);
       ResultSet rs = ps.executeQuery();
       List<Book> books = new ArrayList<>();
       PublisherDAO pubDao = new PublisherDAO();
       AuthorDAO authorDAO = new AuthorDAO();
       while(rs.next()){
           String id = rs.getString("title_id");
           String title = rs.getString("title");
           String pudID = rs.getString("pub_id");
           String note = rs.getString("notes");
           
           Publisher pub = pubDao.getPublisherByID(pudID);
           List<Author> authors = authorDAO.selectAuthorsByBookID(id);
           books.add(new Book(id, title, note,pub,authors));
       }
       rs.close();
       conn.close();
       return books;
    }
    
}
