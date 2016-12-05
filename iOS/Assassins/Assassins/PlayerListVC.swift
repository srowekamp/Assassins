//
//  PlayerListVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class PlayerListVC: UIViewController, UITableViewDelegate, UITableViewDataSource, updateable {

    @IBOutlet weak var playerTableList: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        game?.viewsToUpdate.append(self)
        playerTableList.delegate = self
        playerTableList.dataSource = self
    }
    
    func updateUI() {
        playerTableList.reloadData()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        playerTableList.reloadData()
    }
    
    // MARK: Table View Methods
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1;
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return game!.player_object_list.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "playerCell")!
        cell.textLabel?.text = game?.player_object_list[indexPath.row].real_name
        return cell
    }
}
