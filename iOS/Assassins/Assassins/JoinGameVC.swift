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
        switch gameID.text! {
        case "testgame":
            performSegue(withIdentifier: "joinToGame", sender: nil)
            break
        case "badgame":
            let alert = UIAlertController(title: "Unable to Join Game", message: "Either the game does not exist or you password is incorrect, please try again", preferredStyle: UIAlertControllerStyle.alert)
            alert.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        default:
            // confirm with server that the entered information is valid
            performSegue(withIdentifier: "joinToGame", sender: nil)
            break;
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier! {
        case "joinToGame":
            let lobbyVC = segue.destination as? LobbyVC
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
