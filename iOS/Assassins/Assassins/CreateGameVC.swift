//
//  CreateGameVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class CreateGameVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var gameID: UITextField!
    @IBOutlet weak var gamePassword: UITextField!
    @IBOutlet weak var gameDuration: UIDatePicker!
    
    var gameRadius = 10.0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUpTextField(textField: gameID)
        setUpTextField(textField: gamePassword)
    }
    
    func setUpTextField(textField:UITextField) {
        textField.delegate = self
        textField.autocapitalizationType = .none
        textField.autocorrectionType = .no
    }
    
    // purely to test if variables are being transfered between classes properly. 
    override func viewWillAppear(_ animated: Bool) {
        print(gameRadius)
    }
    
    @IBAction func mapPicker(_ sender: Any) {
        performSegue(withIdentifier: "mapSelect", sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier!{
        case "mapSelect":
            let mapSelectVC = segue.destination as? MapSelectVC
            mapSelectVC?.mainSettingsVC = self
            return
        default:
            return
        }
    }
    
    // MARK: Text Field Methods
    
    // delegate method to close keyboard when the return key is pressed
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
