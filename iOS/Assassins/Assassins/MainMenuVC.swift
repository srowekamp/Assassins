//
//  MainMenuVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 9/28/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class MainMenuVC: UIViewController {
    
    @IBOutlet weak var welcomeLabel: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        if currentUser != nil {
            welcomeLabel.text = "Welcome \(currentUser?.real_name)"
        }
    }
    
    @IBAction func joinGame(_ sender: AnyObject) {
        performSegue(withIdentifier: "joinGame", sender: nil)
    }
    
    @IBAction func createGame(_ sender: AnyObject) {
        performSegue(withIdentifier: "createGame", sender: nil)
    }
    
    @IBAction func tapSettings(_ sender: AnyObject) {
        performSegue(withIdentifier: "settings", sender: nil)
    }

}
