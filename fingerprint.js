/*
 * Copyright (C) 2015, 2016  Green Screens Ltd.
 */


/**
 * Include fingerprint lib in web page and this script to generate fingerprint
 * (GSv4) http://localhost:9080/terminal/lib/client.min.js
 * (post GSv4) http://localhost:9080/terminal/fingerprint.jsp
 */
	
function getFingerprint() {

    const cli = new ClientJS();

    const args = [];
    const data = {};
    const keys = ['isFont', 'getBrowserData', 'getFonts', 'getCanvasPrint', 'getMimeTypes',
      'getSoftwareVersion', 'getCustomFingerprint', 'getFingerprint', 'getPlugins',
      'isFlash', 'getFlashVersion', 'isSilverlight', 'getSilverlightVersion'
    ];

    function fill(v, i, a) {
      if (typeof cli[v] === 'function') {
        let txt = v;
        if (txt.indexOf('get') === 0) {
          txt = txt.charAt(3).toLowerCase() + txt.substr(4);
        }

        let val = cli[v]();
        if (val !== undefined) {
          data[txt] = val;
          if (txt.indexOf('finger') < 0) {
            args.push(val);
          }
        }
      }
      return true;
    }

    let itm, n, els = [];
    for (itm in cli) {
      n = '' + itm;
      if (keys.indexOf(n) < 0 && (n.indexOf('get') === 0 || n.indexOf('is') === 0)) {
        els.push(n);
      }
    }
    els.every(fill);

    const fingerprint = cli.getCustomFingerprint.call(cli, args);
    document.cookie = "fid=" + fingerprint;
    return fingerprint;
  }
  