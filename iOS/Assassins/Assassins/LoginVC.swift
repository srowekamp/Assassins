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

var currentUser:Player?

import UIKit
import Alamofire
import SwiftyJSON

class LoginVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    func switchView(userJSON:JSON) {
        // set up the current user object for use across the app
        currentUser = Player(
             id: userJSON["id"].int!,
             username: userJSON["username"].string!,
             password: userJSON["password"].string!,
             real_Name: userJSON["real_name"].string!,
             image_filename: userJSON["image_filename"].string!,
             games_played: userJSON["games_played"].int!,
             total_kills: userJSON["total_kills"].int!,
             x_location: userJSON["x_location"].int!,
             y_location: userJSON["y_location"].int!
        )
        performSegue(withIdentifier: "loginToMain", sender: self)
    }
    
    func login(username:String, password:String) {
        // the url to login in to the server
        let loginURL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Login"
        let paramaters = ["username":username,"password":password]
        
        // make a request to the server and proccess the data
        Alamofire.request(loginURL, parameters: paramaters).responseJSON { response in
            if let data = response.result.value as? [String:String] {
                if let jsonString = data["account"]?.data(using: .utf8, allowLossyConversion: false) {
                    let json = JSON(data: jsonString)
                    print(json)
                    
                    // check that username and password are correct
                    if (json["username"].string! == username && json["password"].string! == password) {
                        self.switchView(userJSON: json)
                        return
                    } else {
                        self.failedLogin()
                    }
                }
            }
            self.failedLogin()
        }
    }
    
    func failedLogin(){
        let alert = UIAlertController(title: "Login Failed", message: "You username or password is incorrect, please try again.", preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
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
        
        // TODO: check that user name and password are of correct length
        // username must be between 4 and 32 characters
        // password must be between 5 and 32 characters
        login(username: usernameField.text!, password: passwordField.text! )
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
