//
//  JoinGameVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class JoinGameVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var gameID: UITextField!
    @IBOutlet weak var gamePassword: UITextField!
    
    @IBAction func joinGame(_ sender: AnyObject) {
        // eventually use join game server method to test that this is a valid game
        performSegue(withIdentifier: "joinToGame", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier! {
        case "joinToGame":
            let lobbyVC = segue.destination.childViewControllers.first?.childViewControllers.first as? LobbyVC
            lobbyVC?.gameID = gameID.text!
            break
        default:
            break
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        gameID.delegate = self
        gamePassword.delegate = self
        gameID.autocorrectionType = .no
        gameID.autocapitalizationType = .none
        gameID.text = ""
        gamePassword.text = ""
        gamePassword.isSecureTextEntry = true
    }
    
    // MARK: Text Field Delegate Methods
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        textField.resignFirstResponder()
        return true
    }
}
