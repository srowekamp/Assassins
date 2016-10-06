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
}

var user = userInfo()

import UIKit

class LoginVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initTextFields()
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
        case "baduser":
            return false
        case "admin":
            user.adminFlag = true
            user.modFlag = true
            return true
        case "mod":
            user.modFlag = true
            return true
        default:
            // make request to server to check user
            return true
        }
    }
    
    // MARK: Text Field Methods

    // delegate method to close keyboard when the return key is pressed
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
    
    // initial setup
    private func initTextFields() {
        usernameField.delegate = self
        usernameField.autocorrectionType = .no
        usernameField.autocapitalizationType = .none
        
        passwordField.delegate = self
        passwordField.isSecureTextEntry = true
    }
    
}
