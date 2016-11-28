//
//  JoinGameVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class JoinGameVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var gameID: UITextField!
    @IBOutlet weak var gamePassword: UITextField!
    
    let JOIN_GAME_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/JoinGame"
    
    var game:Game?
    
    /* 
 
     4) Join Game: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/JoinGame?gameid=test1&id=1&password=password
     parameters:
     id: ID of the player trying to join the game. (int)
     gameid: ID of the game that you are trying to join. (String)
     password: password of the game you are trying to join. (String)
     
     Returns a JSON object with key "result" with one value indicating what happened
     Result values can be found in JoinGame.java in the Assassins servlet src
     Also within the response is a JSON object with key "game" holding all database info in this game (look at Game.java)

    */
 
    @IBAction func joinGame(_ sender: AnyObject) {
        
        var paramaters = [String:String]()

        paramaters["id"] = "\(currentUser!.id)"
        paramaters["gameid"] = "\(gameID.text!)"
        paramaters["password"] = "\(gamePassword.text!)"

        // Attempt to Join Game
        Alamofire.request(JOIN_GAME_URL, parameters: paramaters).responseJSON { response in
            if response.result.isSuccess {
                let returned_data = JSON(response.result.value!)
                // print("Response JSON:\n\(returned_data)\n")
            
                switch returned_data["result"].string! {
                case "success":
                    
                    let game_data = returned_data["game"]
                    if game_data != JSON.null {
                        self.game = Game(data: game_data)
                        self.game?.printDebugInfo()
                        self.loadLobby()
                    }
                    return
                case "game_not_found":
                    self.popUpAlert(title: "Error", message: "The game you specified was not found.", handler: nil)
                    return
                case "game_started":
                    self.popUpAlert(title: "Error", message: "The game you specified has already started.", handler: nil)
                    return
                case "password_incorrect":
                    self.popUpAlert(title: "Error", message: "The password you entered is incorrect.", handler: nil)
                    return
                default:
                    self.popUpAlert(title: "Error", message: "Unable to join game for unknow reason, error code: \(returned_data["result"].string!)", handler: nil)
                    return
                }
            } else {
                self.popUpAlert(title: "Server Call Failed", message: "Please check your network connection and verify you are using the IOWA STATE VPN", handler: nil)
            }
        }
    }
    
    func loadLobby() {
        if game != nil {
            performSegue(withIdentifier: "joinToGame", sender: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier! {
        case "joinToGame":
            let lobbyVC = segue.destination.childViewControllers.first?.childViewControllers.first as? LobbyVC
            lobbyVC?.game = self.game
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
