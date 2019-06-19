import { Component, OnInit } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
@Component({
  selector: 'threei-consent',
  templateUrl: './consent.component.html',
  styleUrls: ['./consent.component.css']
})
export class ConsentComponent {

  constructor(public snackBar: MatSnackBar) {
      this.snackBar.open('This site uses cookies: <a href="">Find out more.</a>', 'OK', {
         duration: 200000,
         verticalPosition: 'top',
         horizontalPosition: 'right',
      });
  }
}
