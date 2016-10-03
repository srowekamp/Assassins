//
//  CreateAccountVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class CreateAccountVC: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate {
    
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
        confirmPassword.delegate = self
        realName.text = ""
        username.text = ""
        password.text = ""
        confirmPassword.text = ""
        password.isSecureTextEntry = true
        confirmPassword.isSecureTextEntry = true
    }
    
    @IBAction func createAccount(_ sender: AnyObject) {
        
    }
    
    @IBAction func showLoginPage(_ sender: AnyObject) {
        self.dismiss(animated: true) {
            return
        }
    }
    
    // MARK: User Icon Methods
    
    @IBAction func openCameraButton(_ sender: UIButton) {
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.camera
            imagePicker.allowsEditing = false
            self.present(imagePicker, animated: true, completion: nil)
        }
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        userIcon.image = info["UIImagePickerControllerOriginalImage"] as! UIImage?
        self.dismiss(animated: true, completion: nil);
    }
    
    // MARK: Text Field Delegate Methods
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
