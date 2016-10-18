//
//  PlayerListVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class PlayerListVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var playerTableList: UITableView!
    
    @IBAction func leaveGame(_ sender: AnyObject) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func loadAdminView(_ sender: AnyObject) {
        if (user.adminFlag || user.modFlag) {
            performSegue(withIdentifier: "playerListToAdminMod", sender: nil)
        }
    }    
    override func viewDidLoad() {
        super.viewDidLoad()
        playerTableList.delegate = self
        playerTableList.dataSource = self
    }
    
    // MARK: Table View Methods
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1;
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 10;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "playerCell")!
        cell.textLabel?.text = "Player \(indexPath.row)"
        return cell
    }
}
