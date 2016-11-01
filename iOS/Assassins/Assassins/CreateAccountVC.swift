//
//  CreateAccountVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class CreateAccountVC: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate {
    
    struct Constants {
        static let addUserURL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateAccount"
    }
    
    @IBOutlet weak var navBar: UINavigationItem!
    @IBOutlet weak var userIcon: UIImageView!
    @IBOutlet weak var realName: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var confirmPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        prepareTextField(textField: realName, secure: false, capitalType: .words)
        prepareTextField(textField: username, secure: false, capitalType: .none)
        prepareTextField(textField: password, secure: true, capitalType: nil)
        prepareTextField(textField: confirmPassword, secure: true, capitalType: nil)
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
        
        // encode the image a base64 string
        let imageData = UIImageJPEGRepresentation(userIcon.image!, CGFloat(100))
        
        var B64_ImageData:String = (imageData?.base64EncodedString())!
        
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "+", with: "%2B")
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "/", with: "%2F")
        B64_ImageData = B64_ImageData.replacingOccurrences(of: "=", with: "%3D")
        
        //print("\n\n\(B64_ImageData)\n\n")
        
        // set up data for server call
        let parameters = ["username":username.text!,
                          "password":password.text!,
                          "real_name":realName.text!,
                          "b64_jpg":B64_ImageData]
       
        // make server call
        Alamofire.request(Constants.addUserURL, method:.post, parameters: parameters).validate().responseJSON { response in
            switch response.result {
            case .success(let value):
                let json = JSON(value)
                print("JSON: \(json)")
                break
            case .failure(let error):
                print("There was an error: \(error)")
            }
        }

        // for now add the user and move on
//        user.username = username.text!
//        user.password = password.text!
//        user.realName = realName.text!
//        user.image = userIcon.image
        
        // once the user has been created, go to main menu view
        // performSegue(withIdentifier: "createToMenu", sender: nil)
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
    
    // MARK: Text Field Delegate and Other Methods
    
    // sets up a text field based on the given parameters a capitalization type may be specified, but if it is secuer it will be ignored
    func prepareTextField(textField:UITextField, secure:Bool, capitalType:UITextAutocapitalizationType?){
        textField.delegate = self
        textField.text = ""
        
        if secure {
            textField.isSecureTextEntry = true
        } else {
            textField.autocorrectionType = .no
            if capitalType != nil {
                textField.autocapitalizationType = capitalType!
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
