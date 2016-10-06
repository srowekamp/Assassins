//
//  CreateAccountVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class CreateAccountVC: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var navBar: UINavigationItem!
    @IBOutlet weak var userIcon: UIImageView!
    @IBOutlet weak var realName: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var confirmPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        realName.delegate = self
        username.delegate = self
        password.delegate = self
        realName.autocorrectionType = .no
        realName.autocapitalizationType = .words
        username.autocapitalizationType = .none
        username.autocorrectionType = .no
        confirmPassword.delegate = self
        realName.text = ""
        username.text = ""
        password.text = ""
        confirmPassword.text = ""
        password.isSecureTextEntry = true
        confirmPassword.isSecureTextEntry = true
    }
    
    // attempts to create a user account
    // first check if the passwords match then send the information to the server and wait for a response if the account was added
    // the server could return bad account if the user already exists
    
    @IBAction func createAccount(_ sender: AnyObject) {
        if(password.text == confirmPassword.text) {
            // valid user account
        } else {
           popupAlert(title: "Unable to Create Account", message: "Your passwords do not match, please try again.")
        }
        // attempt to create user on server
        // for now add the user and move on
        user.username = username.text!
        user.password = password.text!
        user.realName = realName.text!
        user.image = userIcon.image
        
        // once the user has been created, go to main menu view
        performSegue(withIdentifier: "createToMenu", sender: nil)
    }
    
    @IBAction func showLoginPage(_ sender: AnyObject) {
        self.navigationController!.popViewController(animated: true)
    }
    
    // function to popup an alert
    func popupAlert(title:String, message:String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    // MARK: User Icon Methods
    
    @IBAction func openCameraButton(_ sender: UIButton) {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.camera
            imagePicker.allowsEditing = true
            self.present(imagePicker, animated: true, completion: nil)
        } else {
            let alert = UIAlertController(title: "No Camera", message: "Sorry, but the device you are using does not have a supported camera.", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        userIcon.image = info["UIImagePickerControllerEditedImage"] as! UIImage?
        self.dismiss(animated: true, completion: nil);
    }
    
    // MARK: Text Field Delegate Methods
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
