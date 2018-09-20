import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageacknowledgementsPageComponent } from './imageacknowledgements-page.component';

describe('ImageacknowledgementsPageComponent', () => {
  let component: ImageacknowledgementsPageComponent;
  let fixture: ComponentFixture<ImageacknowledgementsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImageacknowledgementsPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageacknowledgementsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
