/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javasocketserver;

/**
 *
 * @author 30039802
 */
public class User {
    public String userName;
    public byte [] salt;
    public byte [] hash;
    public int iterations;
}
