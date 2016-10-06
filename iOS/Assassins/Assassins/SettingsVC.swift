//
//  SettingsVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class SettingsVC: UIViewController {

    @IBOutlet weak var userImage: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var usernameField: UITextField!
    @IBOutlet weak var currentPasswordField: UITextField!
    @IBOutlet weak var newPasswordField: UITextField!
    @IBOutlet weak var confirmPasswordField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if user.image != nil {
            userImage.image = user.image!
        }
        usernameField.text = user.username
        nameLabel.text = user.realName
    }
    
    @IBAction func changeUser(_ sender: AnyObject) {
        // ask server to change username and wait for response
        user.username = usernameField.text!
    }
    
    @IBAction func changePassword(_ sender: AnyObject) {
        // ask server to change password and wait for response
        user.password = newPasswordField.text!
    }
    
}
