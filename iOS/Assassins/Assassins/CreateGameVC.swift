//
//  CreateGameVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class CreateGameVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var gameID: UITextField!
    @IBOutlet weak var gamePassword: UITextField!
    @IBOutlet weak var gameDuration: UIDatePicker!
    
    // defines the radius of the playing area: min radius is 10m
    var gameRadius = 100.0 // default min
    var xcord:Double = 0.0
    var ycord:Double = 0.0
    
    var gameObject:Game?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUpTextField(textField: gameID)
        setUpTextField(textField: gamePassword)
    }
    
    // configures the text fields
    func setUpTextField(textField:UITextField) {
        textField.delegate = self
        textField.autocapitalizationType = .none
        textField.autocorrectionType = .no
    }
    
    // attempts to create a game on the server
    @IBAction func createGame(_ sender: Any) {
        
        // define variable needed to send request
        let baseURL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateGame"
        var paramaters = [String:Any]()
        paramaters["gameid"] = gameID.text!
        
        if(gamePassword.text != nil) {
            paramaters["password"] = gamePassword.text!
        } else {
            paramaters["password"] = "password" // figure out what default password will be
        }
        
        print("xcord: \(xcord), ycord: \(ycord)")
        
        paramaters["xcenter"] = xcord  // (double)
        paramaters["ycenter"] = ycord // (double)
        paramaters["radius"] = String(Int(gameRadius)) // (int)
        paramaters["hostid"] = String(currentUser!.id)   // current users ID
        paramaters["duration"] = String(Int(gameDuration.countDownDuration)) // time in seconds (int)
        
        // make server requests
        Alamofire.request(baseURL, parameters: paramaters).responseJSON { response in
            if let data = response.result.value as? [String:String] {
                print("Debug Data \(data)")
                if let jsonString = data["game"]?.data(using: .utf8, allowLossyConversion: false) {
                    let json = JSON(data: jsonString)
                    print("Server Request Success, Return JSON: \(json)")
                    
                    self.gameObject = Game(data: json)
                    self.loadGameView()
                    return
                }
                print("There was an error with the server request")
            }
        }
    }
    
    func loadGameView() {
        performSegue(withIdentifier: "createGameToGameView", sender: self)
    }
    
    // opens the map interface to configue the playing area
    @IBAction func mapPicker(_ sender: Any) {
        performSegue(withIdentifier: "mapSelect", sender: self)
    }
    
    // prepares to load map interface
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier!{
        case "mapSelect":
            let mapSelectVC = segue.destination as? MapSelectVC
            
            // gives the map interface a reference to this class so it update the games variables once the play area has been configured
            mapSelectVC?.mainSettingsVC = self
            return
        case "createGameToGameView":
            let gameViewVC = segue.destination.childViewControllers.first?.childViewControllers.first as? LobbyVC
            gameViewVC?.gameObject = self.gameObject
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
