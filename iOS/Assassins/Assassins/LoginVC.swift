//
//  LoginVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 9/28/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

struct userInfo {
    var username = "baduser"
    var password = "badpass"
    var realName = "noName"
    var adminFlag = false
    var modFlag = false
    var image:UIImage?
}

var user = userInfo()

import UIKit
import Alamofire
import SwiftyJSON

class LoginVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    let web = WebRequest()
    
    func login() {
        let loginURL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Login"
        let paramaters = ["username":"admin","password":"password"]
        Alamofire.request(loginURL, parameters: paramaters).responseJSON { response in
            //print(response.request)  // original URL request
            //print(response.response) // HTTP URL response
            //print(response.data)     // server data
            //print(response.result)   // result of response serialization
            
            if let data = response.result.value {
                let json = JSON(data: data)
                if let username = json["account"]["username"].string {
                    //Now you got your value
                }
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        login()
        initTextFields()
        web.getUser {
            return
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        usernameField.text = ""
        passwordField.text = ""
    }
    
    // called when he user wants to create a new account
    @IBAction func showCreateAccount(_ sender: AnyObject) {
        performSegue(withIdentifier: "createAccount", sender: self)
    }

    // called when the user tries to log in
    @IBAction func tapLogin(_ sender: AnyObject) {
        if checkUser(username: usernameField.text!, password: passwordField.text!) {
            user.username = usernameField.text!
            user.password = passwordField.text!
            performSegue(withIdentifier: "loginToMain", sender: self)
        } else {
            let alert = UIAlertController(title: "Login Failed", message: "You username or password is incorrect, please try again.", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    // verifiy a user is valid before loggin in
    // there are also three dummy usernames to test app features
    func checkUser(username: String, password: String) -> Bool{
        if(username == "" || password == "") {
            return false
        }
        switch(username){
        case "admin2":
            user.adminFlag = true
            user.modFlag = true
            return true
        case "mod":
            user.modFlag = true
            return true
        default:
            user.adminFlag = false
            user.modFlag = false
            
            // We will eventually have a method that asks the server to validate a username and password combination. 
            // Right now we are just downloading a table of users and looking at the first item in the returned JSON
            
            // check if username and password are correct the web. items are from the WebRequest
            print("\(web.username) , \(web.password)")
            if username == web.username && password == web.password {
                return true
            }
            return false
        }
    }
    
    // MARK: Text Field Methods

    // delegate method to close keyboard when the return key is pressed
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
    
    // MARK: Initial Setup Functions
    
    private func initTextFields() {
        usernameField.delegate = self
        usernameField.autocorrectionType = .no
        usernameField.autocapitalizationType = .none
        
        passwordField.delegate = self
        passwordField.isSecureTextEntry = true
    }
    
}
