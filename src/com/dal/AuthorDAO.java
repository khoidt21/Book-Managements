/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dal;

import com.context.DBContext;
import com.entity.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class AuthorDAO {
    
    //return the list of all authors
    public List<Author> selectAll()throws Exception {
        String select = "select * from authors";
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(select);
        ResultSet rs = ps.executeQuery();
        List<Author> authors = new ArrayList<>();
        while(rs.next()){
            String id = rs.getString("au_id");
            String name = rs.getString("au_name");
            String address = rs.getString("au_address");
            authors.add(new Author(id, name, address));
        }
        rs.close();
        conn.close();
        return authors;
    }
    //return information of an Author by a given author id, 
    //return null if a given author id does not exists
    public Author getAuthorByID(String aid) throws Exception {
        List<Author> authors = selectAll();
        for(Author p : authors){
            if(p.getId().equalsIgnoreCase(aid)){
                return p;
            }
        }
        return null;
    }
    
    //return a list of Authors who wrote the given book id
    public List<Author> selectAuthorsByBookID(String bookID)throws Exception {
        String select = "select a.* from authors a,TitleAuthor ta where a.au_id = ta.au_id and ta.title_id = ?";
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setString(1,bookID);
        ResultSet rs = ps.executeQuery();
        List<Author> authors = new ArrayList<>();
        while(rs.next()){
            String id = rs.getString("au_id");
            String name = rs.getString("au_name");
            String address = rs.getString("au_address");
            authors.add(new Author(id, name, address));
        }
        rs.close();
        conn.close();
        return authors;
    } 
}
