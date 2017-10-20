	public CanvasContainer() {

		_canvas = Canvas.createIfSupported();
		
		initWidget(_binder.createAndBindUi(this));		
		
		final int tablet = 768;
		final int phone = 480;
		
		int width = 900;
		int height = 480;		
		double ratio = 8f/15f;
		
		final int viewPortSize = Window.getClientWidth();
		
		if (viewPortSize > tablet) {			
			;
		}
		else if (viewPortSize > phone) {
			final int tabletOffset = 200;
			width = tablet - tabletOffset;
		}
		else {					
			final int phoneOffset = 50;
			width = viewPortSize - phoneOffset;
		}
		
		height = (int) ((double)width * ratio);
		
		_scrollPanel.setPixelSize(width, height);
		_canvas.setCoordinateSpaceWidth(width);
		_canvas.setCoordinateSpaceHeight(height);	
	}
