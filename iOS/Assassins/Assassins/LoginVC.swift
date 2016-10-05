//
//  LoginVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 9/28/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class LoginVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        usernameField.delegate = self
        passwordField.delegate = self
        passwordField.isSecureTextEntry = true
    }
    
    override func viewDidAppear(_ animated: Bool) {
        usernameField.text = ""
        passwordField.text = ""
    }
    
    @IBAction func showCreateAccount(_ sender: AnyObject) {
        performSegue(withIdentifier: "createAccount", sender: self)
    }

    @IBAction func tapLogin(_ sender: AnyObject) {
        if checkUser(username: usernameField.text!, password: passwordField.text!) {
            performSegue(withIdentifier: "loginToMain", sender: self)
        } else {
            let alert = UIAlertController(title: "Login Failed", message: "You username or password is incorrect, please try again.", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
        
    }
    
    func checkUser(username: String, password: String) -> Bool{
        if(username == "baduser" && password == "badpass"){
            return false
        } else {
            // check user by calling server
            return true
        }
    }
    
    // MARK: Text Field Delegate Methods

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
    
}
